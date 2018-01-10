function setDate () {
    var el = document.getElementById("title");


}

$(document).ready(function filltable () {
    var tableBody = document.getElementById("tableBody");


    for (var i = 0; i < 10; i++) {
        var tr = document.createElement('TR');
        tr.className = "item";
        var items = new Array(" "+ (1+i), " "+ 2+i, "Owner"+i, "Title"+i, 100+i*2, 50+i);
        for (var j = 0; j < 6; j++) {
            var td = document.createElement('TD');
            td.appendChild(document.createTextNode(items[j]));
            tr.appendChild(td);
        }
        tableBody.appendChild(tr);

    }

});

$(document).ready(function () {
    $(".search_bar .row a").on("click", function () {
        $('.search_bar .row a').removeClass();
        $(this).addClass('active');



    });
});





