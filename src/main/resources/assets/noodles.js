window.onload = startup;

BASE_URL = "http://localhost:8080/";
BASE_TILE_SIZE = 0.0125;

PIXELS_PER_TILE = 200;

INITIAL_ZOOM = 3;
INITIAL_LAT = 44.2;
INITIAL_LON = -72.4;

var canvas_width = 0;
var canvas_height = 0;
var center_lat = INITIAL_LAT + getTileSize(INITIAL_ZOOM) / 2;
var center_lon = INITIAL_LON + getTileSize(INITIAL_ZOOM) / 2;
var current_zoom = INITIAL_ZOOM;
var ctx = null;

function getTileSize(zoom) {
    return Math.pow(2, zoom) * BASE_TILE_SIZE;
}

function startup() {
    var canvas = document.getElementById("map-canvas");
    canvas_width = canvas.width;
    canvas_height = canvas.height;

    ctx = canvas.getContext("2d");
    ctx.fillStyle = "#eeeeff";
    ctx.fillRect(0, 0, canvas_width, canvas_height);

    getTile(INITIAL_LON, INITIAL_LAT, INITIAL_ZOOM);
}

function latLonToPix(lat, lon) {
    var tileSize = getTileSize(current_zoom);
    var result = {
        x: (canvas_width / 2) + (PIXELS_PER_TILE * (center_lon - lon) / tileSize),
        y: (canvas_height / 2) + (PIXELS_PER_TILE * (center_lat - lat) / tileSize)
    };
    console.log("Offset: (" + (center_lon - lon) / tileSize + ", " + (center_lat - lat) / tileSize + ")");
    return result;
}

function getTile(lon, lat, zoom) {
    //var json = [{"oneWay":false,"building":"","highway":"service","name":"","p1":{"lat":44.20062,"lon":-72.388442},"p2":{"lat":44.200383,"lon":-72.387669}},{"oneWay":false,"building":"","highway":"service","name":"Rbb Drive","p1":{"lat":44.200094,"lon":-72.399687},"p2":{"lat":44.200196,"lon":-72.39957}},{"oneWay":false,"building":"","highway":"service","name":"Rbb Drive","p1":{"lat":44.200196,"lon":-72.39957},"p2":{"lat":44.200234,"lon":-72.399396}},{"oneWay":false,"building":"","highway":"service","name":"Rbb Drive","p1":{"lat":44.200234,"lon":-72.399396},"p2":{"lat":44.20023,"lon":-72.399206}},{"oneWay":false,"building":"","highway":"service","name":"Rbb Drive","p1":{"lat":44.20023,"lon":-72.399206},"p2":{"lat":44.200155,"lon":-72.399025}},{"oneWay":false,"building":"","highway":"service","name":"","p1":{"lat":44.200631,"lon":-72.399761},"p2":{"lat":44.201133,"lon":-72.39938}},{"oneWay":false,"building":"","highway":"service","name":"","p1":{"lat":44.201133,"lon":-72.39938},"p2":{"lat":44.201524,"lon":-72.398843}},{"oneWay":false,"building":"","highway":"service","name":"","p1":{"lat":44.201524,"lon":-72.398843},"p2":{"lat":44.201915,"lon":-72.398192}},{"oneWay":false,"building":"","highway":"service","name":"","p1":{"lat":44.201915,"lon":-72.398192},"p2":{"lat":44.201971,"lon":-72.397845}},{"oneWay":false,"building":"","highway":"residential","name":"Reservoir Road","p1":{"lat":44.203681,"lon":-72.399927},"p2":{"lat":44.20426,"lon":-72.399707}},{"oneWay":false,"building":"","highway":"residential","name":"Reservoir Road","p1":{"lat":44.20426,"lon":-72.399707},"p2":{"lat":44.204671,"lon":-72.39957}},{"oneWay":false,"building":"","highway":"residential","name":"Reservoir Road","p1":{"lat":44.204671,"lon":-72.39957},"p2":{"lat":44.204744,"lon":-72.399538}},{"oneWay":false,"building":"","highway":"residential","name":"Reservoir Road","p1":{"lat":44.204744,"lon":-72.399538},"p2":{"lat":44.204954,"lon":-72.399476}},{"oneWay":false,"building":"","highway":"residential","name":"Reservoir Road","p1":{"lat":44.204954,"lon":-72.399476},"p2":{"lat":44.20502,"lon":-72.399458}},{"oneWay":false,"building":"","highway":"residential","name":"Reservoir Road","p1":{"lat":44.20502,"lon":-72.399458},"p2":{"lat":44.205085,"lon":-72.39944}},{"oneWay":false,"building":"","highway":"residential","name":"Reservoir Road","p1":{"lat":44.205085,"lon":-72.39944},"p2":{"lat":44.205286,"lon":-72.399372}},{"oneWay":false,"building":"","highway":"residential","name":"Reservoir Road","p1":{"lat":44.205286,"lon":-72.399372},"p2":{"lat":44.206696,"lon":-72.398837}},{"oneWay":false,"building":"","highway":"residential","name":"Reservoir Road","p1":{"lat":44.206696,"lon":-72.398837},"p2":{"lat":44.206897,"lon":-72.398766}},{"oneWay":false,"building":"","highway":"residential","name":"Reservoir Road","p1":{"lat":44.206897,"lon":-72.398766},"p2":{"lat":44.207304,"lon":-72.398635}},{"oneWay":false,"building":"","highway":"residential","name":"Reservoir Road","p1":{"lat":44.207304,"lon":-72.398635},"p2":{"lat":44.207717,"lon":-72.398522}},{"oneWay":false,"building":"","highway":"residential","name":"Reservoir Road","p1":{"lat":44.207717,"lon":-72.398522},"p2":{"lat":44.207923,"lon":-72.398456}},{"oneWay":false,"building":"","highway":"residential","name":"Reservoir Road","p1":{"lat":44.207923,"lon":-72.398456},"p2":{"lat":44.208126,"lon":-72.398378}},{"oneWay":false,"building":"","highway":"residential","name":"Reservoir Road","p1":{"lat":44.208126,"lon":-72.398378},"p2":{"lat":44.20853,"lon":-72.398202}},{"oneWay":false,"building":"","highway":"residential","name":"Reservoir Road","p1":{"lat":44.20853,"lon":-72.398202},"p2":{"lat":44.209126,"lon":-72.397909}},{"oneWay":false,"building":"","highway":"residential","name":"Reservoir Road","p1":{"lat":44.209126,"lon":-72.397909},"p2":{"lat":44.209326,"lon":-72.397818}},{"oneWay":false,"building":"","highway":"residential","name":"Reservoir Road","p1":{"lat":44.209326,"lon":-72.397818},"p2":{"lat":44.209526,"lon":-72.397734}},{"oneWay":false,"building":"","highway":"residential","name":"Reservoir Road","p1":{"lat":44.209526,"lon":-72.397734},"p2":{"lat":44.209728,"lon":-72.397658}},{"oneWay":false,"building":"","highway":"residential","name":"Reservoir Road","p1":{"lat":44.209728,"lon":-72.397658},"p2":{"lat":44.209934,"lon":-72.397596}},{"oneWay":false,"building":"","highway":"residential","name":"Reservoir Road","p1":{"lat":44.209934,"lon":-72.397596},"p2":{"lat":44.210139,"lon":-72.397546}},{"oneWay":false,"building":"","highway":"residential","name":"Reservoir Road","p1":{"lat":44.210139,"lon":-72.397546},"p2":{"lat":44.210347,"lon":-72.397509}},{"oneWay":false,"building":"","highway":"residential","name":"Reservoir Road","p1":{"lat":44.210347,"lon":-72.397509},"p2":{"lat":44.210768,"lon":-72.397458}},{"oneWay":false,"building":"","highway":"service","name":"","p1":{"lat":44.201145,"lon":-72.389025},"p2":{"lat":44.201665,"lon":-72.388446}},{"oneWay":false,"building":"","highway":"service","name":"","p1":{"lat":44.201665,"lon":-72.388446},"p2":{"lat":44.202138,"lon":-72.38798}},{"oneWay":false,"building":"","highway":"service","name":"Bellardine Row","p1":{"lat":44.20502,"lon":-72.399458},"p2":{"lat":44.20483,"lon":-72.398573}},{"oneWay":false,"building":"","highway":"service","name":"Bellardine Row","p1":{"lat":44.20483,"lon":-72.398573},"p2":{"lat":44.204706,"lon":-72.398376}},{"oneWay":false,"building":"","highway":"service","name":"Bellardine Row","p1":{"lat":44.204706,"lon":-72.398376},"p2":{"lat":44.204586,"lon":-72.398066}},{"oneWay":false,"building":"","highway":"service","name":"Bellardine Row","p1":{"lat":44.204586,"lon":-72.398066},"p2":{"lat":44.204391,"lon":-72.397703}},{"oneWay":false,"building":"","highway":"service","name":"Bellardine Row","p1":{"lat":44.204391,"lon":-72.397703},"p2":{"lat":44.204248,"lon":-72.397439}},{"oneWay":false,"building":"","highway":"service","name":"Bellardine Row","p1":{"lat":44.204248,"lon":-72.397439},"p2":{"lat":44.204186,"lon":-72.397364}},{"oneWay":false,"building":"","highway":"service","name":"Bellardine Row","p1":{"lat":44.204186,"lon":-72.397364},"p2":{"lat":44.204162,"lon":-72.397228}},{"oneWay":false,"building":"","highway":"service","name":"Bellardine Row","p1":{"lat":44.204162,"lon":-72.397228},"p2":{"lat":44.204172,"lon":-72.39703}},{"oneWay":false,"building":"","highway":"service","name":"Bellardine Row","p1":{"lat":44.204172,"lon":-72.39703},"p2":{"lat":44.204216,"lon":-72.396819}},{"oneWay":false,"building":"","highway":"service","name":"Bellardine Row","p1":{"lat":44.204216,"lon":-72.396819},"p2":{"lat":44.204251,"lon":-72.396454}},{"oneWay":false,"building":"","highway":"service","name":"Bellardine Row","p1":{"lat":44.204251,"lon":-72.396454},"p2":{"lat":44.204272,"lon":-72.396128}},{"oneWay":false,"building":"","highway":"service","name":"Bellardine Row","p1":{"lat":44.204272,"lon":-72.396128},"p2":{"lat":44.204161,"lon":-72.395584}},{"oneWay":false,"building":"","highway":"service","name":"Bellardine Row","p1":{"lat":44.204161,"lon":-72.395584},"p2":{"lat":44.204059,"lon":-72.395259}},{"oneWay":false,"building":"","highway":"service","name":"Bellardine Row","p1":{"lat":44.204059,"lon":-72.395259},"p2":{"lat":44.203977,"lon":-72.395032}},{"oneWay":false,"building":"","highway":"service","name":"Bellardine Row","p1":{"lat":44.203977,"lon":-72.395032},"p2":{"lat":44.203865,"lon":-72.394737}},{"oneWay":false,"building":"","highway":"service","name":"Bellardine Row","p1":{"lat":44.203865,"lon":-72.394737},"p2":{"lat":44.203698,"lon":-72.394442}},{"oneWay":false,"building":"","highway":"service","name":"Bellardine Row","p1":{"lat":44.203698,"lon":-72.394442},"p2":{"lat":44.203453,"lon":-72.393996}},{"oneWay":false,"building":"","highway":"service","name":"Bellardine Row","p1":{"lat":44.203453,"lon":-72.393996},"p2":{"lat":44.203237,"lon":-72.393739}},{"oneWay":false,"building":"","highway":"service","name":"Bellardine Row","p1":{"lat":44.203237,"lon":-72.393739},"p2":{"lat":44.203043,"lon":-72.393506}},{"oneWay":false,"building":"","highway":"service","name":"Bellardine Row","p1":{"lat":44.203043,"lon":-72.393506},"p2":{"lat":44.202884,"lon":-72.39325}},{"oneWay":false,"building":"","highway":"service","name":"Bellardine Row","p1":{"lat":44.202884,"lon":-72.39325},"p2":{"lat":44.202654,"lon":-72.392705}},{"oneWay":false,"building":"","highway":"service","name":"Bellardine Row","p1":{"lat":44.202654,"lon":-72.392705},"p2":{"lat":44.202471,"lon":-72.392251}},{"oneWay":false,"building":"","highway":"service","name":"Bellardine Row","p1":{"lat":44.202471,"lon":-72.392251},"p2":{"lat":44.202396,"lon":-72.391858}},{"oneWay":false,"building":"","highway":"service","name":"Bellardine Row","p1":{"lat":44.202396,"lon":-72.391858},"p2":{"lat":44.202335,"lon":-72.39144}},{"oneWay":false,"building":"","highway":"service","name":"Bellardine Row","p1":{"lat":44.202335,"lon":-72.39144},"p2":{"lat":44.202259,"lon":-72.390963}},{"oneWay":false,"building":"","highway":"service","name":"Bellardine Row","p1":{"lat":44.202259,"lon":-72.390963},"p2":{"lat":44.202174,"lon":-72.390752}},{"oneWay":false,"building":"","highway":"service","name":"Bellardine Row","p1":{"lat":44.202174,"lon":-72.390752},"p2":{"lat":44.201969,"lon":-72.390442}},{"oneWay":false,"building":"","highway":"service","name":"Bellardine Row","p1":{"lat":44.201969,"lon":-72.390442},"p2":{"lat":44.201849,"lon":-72.39023}},{"oneWay":false,"building":"","highway":"service","name":"Bellardine Row","p1":{"lat":44.201849,"lon":-72.39023},"p2":{"lat":44.201628,"lon":-72.389966}},{"oneWay":false,"building":"","highway":"service","name":"Bellardine Row","p1":{"lat":44.201628,"lon":-72.389966},"p2":{"lat":44.201434,"lon":-72.389657}},{"oneWay":false,"building":"","highway":"service","name":"Bellardine Row","p1":{"lat":44.201434,"lon":-72.389657},"p2":{"lat":44.201314,"lon":-72.389445}},{"oneWay":false,"building":"","highway":"service","name":"Bellardine Row","p1":{"lat":44.201314,"lon":-72.389445},"p2":{"lat":44.201217,"lon":-72.389218}},{"oneWay":false,"building":"","highway":"service","name":"Bellardine Row","p1":{"lat":44.201217,"lon":-72.389218},"p2":{"lat":44.201145,"lon":-72.389025}},{"oneWay":false,"building":"","highway":"service","name":"Bellardine Row","p1":{"lat":44.201145,"lon":-72.389025},"p2":{"lat":44.201119,"lon":-72.388953}},{"oneWay":false,"building":"","highway":"service","name":"Bellardine Row","p1":{"lat":44.201119,"lon":-72.388953},"p2":{"lat":44.201001,"lon":-72.388739}},{"oneWay":false,"building":"","highway":"service","name":"Bellardine Row","p1":{"lat":44.201001,"lon":-72.388739},"p2":{"lat":44.200822,"lon":-72.388488}},{"oneWay":false,"building":"","highway":"service","name":"Bellardine Row","p1":{"lat":44.200822,"lon":-72.388488},"p2":{"lat":44.20062,"lon":-72.388442}},{"oneWay":false,"building":"","highway":"service","name":"Bellardine Row","p1":{"lat":44.20062,"lon":-72.388442},"p2":{"lat":44.200452,"lon":-72.388472}},{"oneWay":false,"building":"","highway":"service","name":"Bellardine Row","p1":{"lat":44.200452,"lon":-72.388472},"p2":{"lat":44.200166,"lon":-72.388739}},{"oneWay":false,"building":"","highway":"service","name":"Bellardine Row","p1":{"lat":44.200166,"lon":-72.388739},"p2":{"lat":44.200059,"lon":-72.388953}}]
    get("api/noodle/tile/" + lon + "/" + lat + "/" + zoom, function (xhr) {
        console.log(xhr.responseText);
        var json = JSON.parse(xhr.responseText);
        console.log(json);
        console.log(canvas_width);
        console.log(canvas_height);

        console.log("Center: " + center_lon + ", " + center_lat);
        ctx.fillStyle = "#000000";
        json.forEach(function (drawWay) {
            var p1 = drawWay.p1;
            var p2 = drawWay.p2;
            var x1 = latLonToPix(p1.lat, p1.lon);
            var x2 = latLonToPix(p2.lat, p2.lon);
            ctx.moveTo(x1.x, x1.y);
            ctx.lineTo(x2.x, x2.y);
        });
        ctx.stroke();
    });
}

function ping() {
    get("api/noodle/root", function (xhr) {
        console.log("In callback!");
        if (xhr.responseText === "Oops!") {
            alert("Ping!");
        }
    });
}

function get(url, cb) {
    var processRequest = function() {
        if (xhr.readyState == 4 && xhr.status == 200) {
            cb(xhr);
        }
    };

    var xhr = new XMLHttpRequest();
    xhr.onreadystatechange = processRequest;
    xhr.open("GET", BASE_URL + url, true);
    xhr.send();

}
