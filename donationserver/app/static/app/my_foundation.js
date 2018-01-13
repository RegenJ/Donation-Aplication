//draft function
$(document).ready(function filltable () {
    var tableBody = document.getElementById("tableBody");

    for (var i = 0; i < 10; i++) {
        var tr = document.createElement('TR');
        tr.className = "item";
        var items = new Array(" "+ 2+i, "Title"+i, 50+i);
        for (var j = 0; j < 3; j++) {
            var td = document.createElement('TD');
            td.appendChild(document.createTextNode(items[j]));
            tr.appendChild(td);
        }
        tableBody.appendChild(tr);

    }

});