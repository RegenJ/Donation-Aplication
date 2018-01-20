//draft function
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



//switch active search bar element
$(document).ready(function () {
    $(".search_bar .row a").on("click", function () {
        $('.search_bar .row a').removeClass();
        $(this).addClass('active');

    });
});


// form and send request

function combineAndSend() {
    console.log("begin...");
    var form = document.createElement("form");
    form.setAttribute("method", "POST" );
    form.setAttribute("action", "https://donationserver.herokuapp.com/donateothers/");

    var hiddenField1 = document.createElement("input");
    hiddenField1.setAttribute("type", "hidden");


    //( $('a.search_bar').attr('class') === "active")
    if ( $('#user').attr('class') === "active") {
        hiddenField1.setAttribute("name", 'key');
        //hiddenField1.setAttribute("value", $('#user').text());
    } else {
        hiddenField1.setAttribute("name", 'key');
        //hiddenField1.setAttribute("value", $('#title').text());
    }

    hiddenField1.setAttribute("value", $('#searchVal').val());

   // alert("Value: " + $('#searchVal').val() );
    form.appendChild(hiddenField1);

    document.body.appendChild(form);

    //alert("hidden: " + hiddenField1);
    console.log("jujfv");
    form.submit();
}


