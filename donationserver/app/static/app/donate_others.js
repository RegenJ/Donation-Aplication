// //draft function
// $(document).ready(function filltable () {
//     var tableBody = document.getElementById("tableBody");
//
//     for (var i = 0; i < 10; i++) {
//         var tr = document.createElement('TR');
//         tr.className = "item";
//         var items = new Array(" "+ (1+i), " "+ 2+i, "Owner"+i, "Title"+i, 100+i*2, 50+i);
//         for (var j = 0; j < 6; j++) {
//             var td = document.createElement('TD');
//             td.appendChild(document.createTextNode(items[j]));
//             tr.appendChild(td);
//         }
//         tableBody.appendChild(tr);
//
//     }
//
// });



//switch active search bar element
$(document).ready(function () {
    $(".search_bar .row a").on("click", function () {
        $('.search_bar .row a').removeClass();
        $(this).addClass('active');

    });
});


//send request
function combineAndSendForms() {
    var $newForm = $("<form></form>")    // our new form.
        .attr({method: "POST", action: "https://donationserver.herokuapp.com/donateothers/"}) // customise as required
    ;
    var key = document.getElementById("search_key").value;
    $newForm.append($("<input type=\"hidden\" />")   // create a new hidden field
        .attr('name', "search_key")   // with the same name (watch out for duplicates!)
        .val(key)        // and the same value
    );
    $newForm
        .appendTo(document.body)  // not sure if this is needed?
        .submit()                 // submit the form
    ;
}


function sendBTC(gathering_id) {
    var $newForm = $("<form></form>")    // our new form.
            .attr({method: "POST", action: "https://donationserver.herokuapp.com/paybtc/"}) // customise as required
        ;
        $newForm.append($("<input type=\"hidden\" />")   // create a new hidden field
                .attr('name', "gathering_id")   // with the same name (watch out for duplicates!)
                .val(gathering_id)        // and the same value
            );
        $newForm
            .appendTo(document.body)  // not sure if this is needed?
            .submit()                 // submit the form
        ;
}

