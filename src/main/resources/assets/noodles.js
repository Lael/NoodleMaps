BASE_URL = 'http://localhost:8080/';
BASE_TILE_SIZE = 0.0125;

SCROLL_DIRECTION = -10;
TILES_PER_DEGREE = 10;

INITIAL_LATLON = {
    lat: 47.7, // north
    lon: 18.5  // east
};

var map_div = null;
var canvas = null;
var ctx = null;

var shapes = {
    pin1: null,
    pin2: null,
    tiles: [],
    tileIds: {}
};

function getMousePos(evt) {
    var rect = canvas.getBoundingClientRect();
    return {
        x: (evt.clientX - rect.left) / (rect.right - rect.left) * canvas.width,
        y: (evt.clientY - rect.top) / (rect.bottom - rect.top) * canvas.height
    };
}

function tileId(latlon) {
    var x = (latlon.lat * TILES_PER_DEGREE);
    var y = (latlon.lon * TILES_PER_DEGREE);

    id = x.toString() + "_" + y.toString();
    return id;
}

var converter = {
    zoom: 20000.0, // pixels per degree
    nwLatLon: {
        lat: INITIAL_LATLON.lat + 0.05,
        lon: INITIAL_LATLON.lon - 0.05
    },
    needTiles: function() {
        var box = {
            north: Math.ceil(10 * this.nwLatLon.lat),
            south: Math.floor(10 * (this.nwLatLon.lat - canvas.height / this.zoom)),
            west: Math.floor(10 * this.nwLatLon.lon),
            east: Math.ceil(10 * (this.nwLatLon.lon + canvas.height / this.zoom))
        };
        for (var i = box.south; i < box.north; i ++) {
            for (var j = box.west; j < box.east; j ++ ) {
                var latlon = {
                    lat: i / 10.0,
                    lon: j / 10.0
                };
                var id = tileId(latlon);
                if (!shapes.tileIds.hasOwnProperty(id)) {
                    getTile(latlon);
                }
            }
        }
    },
    panByPixels: function(xy) {
        this.nwLatLon.lat -= xy.y / this.zoom;
        this.nwLatLon.lon += xy.x / this.zoom;
        converter.needTiles();
    },
    toLatLon: function(xy) {
        return {
            lat: this.nwLatLon.lat - xy.y / this.zoom,
            lon: this.nwLatLon.lon + xy.x / this.zoom
        }
    },
    toPixels: function(latlon) {
        return {
            x: (latlon.lon - this.nwLatLon.lon) * this.zoom,
            y: -1 * (latlon.lat - this.nwLatLon.lat) * this.zoom
        }
    }
};

var nav = {
    dragged: false,
    clicked: false,
    dragOffset: null,
    dropPin: function(xy) {
        if (!shapes.pin1) {
            shapes.pin1 = converter.toLatLon(xy);
        } else if (!shapes.pin2) {
            shapes.pin2 = converter.toLatLon(xy);
        } else {
            shapes.pin1 = null;
            shapes.pin2 = null;
        }
        draw()
    },
    down: function(e) {
        nav.clicked = true;
        nav.dragOffset = getMousePos(e);
    },
    drag: function(e) {
        if (nav.clicked) {
            nav.dragged = true;
            var mousePos = getMousePos(e);
            converter.panByPixels({
                x: nav.dragOffset.x - mousePos.x,
                y: nav.dragOffset.y - mousePos.y
            });
            nav.dragOffset = mousePos;
            draw();
        }
    },
    up: function(e) {
        if (!nav.dragged) {
            nav.dropPin(getMousePos(e));
        }
        nav.clicked = false;
        nav.dragged = false;
    },
    scroll: function(e) {
        var mousePos = getMousePos(e);
        var mousePosLatLon = converter.toLatLon(mousePos);
        converter.zoom += SCROLL_DIRECTION * e.deltaY;
        if (converter.zoom > 20000) {
            converter.zoom = 20000;
        } else if (converter.zoom < 100) {
            converter.zoom = 100;
        }

        var newMousePos = converter.toPixels(mousePosLatLon);
        var diff = {
            x: newMousePos.x - mousePos.x,
            y: newMousePos.y - mousePos.y
        };
        converter.panByPixels(diff);
        draw();
    },
    resize: function() {
        var off = {
            x: (canvas.width - 2 * map_div.clientWidth) / 2,
            y: (canvas.height - 2 * map_div.clientHeight) / 2
        };
        converter.panByPixels(off);
        draw();
    }
};

window.onload = startup;

function startup() {

    map_div = document.getElementById('map-div');
    canvas = document.getElementById('map-canvas');

    canvas.addEventListener('mousedown',        nav.down);
    canvas.addEventListener('mousemove',        nav.drag);
    canvas.addEventListener('mouseup',          nav.up);
    canvas.addEventListener('DOMMouseScroll',   nav.scroll);
    canvas.addEventListener('mousewheel',       nav.scroll);
    window.addEventListener('resize',           nav.resize);

    ctx = canvas.getContext('2d');

    draw();
    converter.needTiles();
    draw();
}

function drawPin(xy, color) {
    ctx.beginPath();
    ctx.moveTo(xy.x, xy.y);
    ctx.lineTo(xy.x - 20, xy.y - 40);
    ctx.lineTo(xy.x + 20, xy.y - 40);
    ctx.closePath();
    ctx.arc(xy.x, xy.y - 40, 20, 0, 2 * Math.PI);
    ctx.fillStyle = color;
    ctx.fill();
    ctx.beginPath();
    ctx.fillStyle = '#222222';
    ctx.arc(xy.x, xy.y - 40, 10, 0, 2 * Math.PI);
    ctx.fill();
}

function draw() {
    canvas.width = 2 * map_div.clientWidth;
    canvas.height = 2 * map_div.clientHeight;

    ctx.fillStyle = '#bbddff';
    ctx.fillRect(0, 0, canvas.width, canvas.height);

    ctx.fillStyle = '#2222aa';
    shapes.tiles.forEach(function (tile) {
        tile.forEach(function (drawWay) {
            if (drawWay.p1 && drawWay.p2) {
                var p1pix = converter.toPixels(drawWay.p1);
                var p2pix = converter.toPixels(drawWay.p2);
                ctx.moveTo(p1pix.x, p1pix.y);
                ctx.lineTo(p2pix.x, p2pix.y);
            } else {
                console.log(drawWay);
            }
        });
    });
    ctx.stroke();

    if (shapes.pin1) {
        var pin1pix = converter.toPixels(shapes.pin1);
        drawPin(pin1pix, '#22aa22');
    }

    if (shapes.pin2) {
        var pin2pix = converter.toPixels(shapes.pin2);
        drawPin(pin2pix, '#aa2222');
    }
}

function getTile(latlon) {
    var id = tileId(latlon);
    shapes.tileIds[id] = 'pending';
    console.log('Asking for (' + latlon.lat + ', ' + latlon.lon + ').');
    get('api/noodle/tile/' + latlon.lat + '/' + latlon.lon, function (xhr) {
        var json = JSON.parse(xhr.responseText);
        console.log(json);
        shapes.tiles.push(json.ways);
        shapes.tileIds[json.id] = 'done';
        draw();
    });
}

function get(url, cb) {
    var processRequest = function() {
        if (xhr.readyState === 4 && xhr.status === 200) {
            cb(xhr);
        }
    };

    var xhr = new XMLHttpRequest();
    xhr.onreadystatechange = processRequest;
    xhr.open('GET', BASE_URL + url, true);
    xhr.send();

}
