var xhr = new XMLHttpRequest();
xhr.open('GET', "http://localhost:8080/noodle/tile/-72.4/44.2/0", true);
xhr.send();
 
xhr.onreadystatechange = processRequest;
 
function processRequest(e) {
    if (xhr.readyState == 4 && xhr.status == 200) {
        var response = JSON.parse(xhr.responseText);
        alert(response.ip);
    }
}