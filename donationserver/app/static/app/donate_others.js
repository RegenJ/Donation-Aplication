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


//send request
function combineAndSendForms() {
    var $newForm = $("<form></form>")    // our new form.
        .attr({method: "POST", action: "https://donationserver.herokuapp.com/search/"}) // customise as required
    ;
    $(":input:not(:submit, :button)").each(function () {  // grab all the useful inputs
        $newForm.append($("<input type=\"hidden\" />")   // create a new hidden field
            .attr('name', this.name)   // with the same name (watch out for duplicates!)
            .val($(this).val())        // and the same value
        );
    });
    var desc = document.getElementsByName("desc").value;
    $newForm.append($("<input type=\"hidden\" />")   // create a new hidden field
        .attr('name', "desc")   // with the same name (watch out for duplicates!)
        .val(desc)        // and the same value
    );
    $newForm
        .appendTo(document.body)  // not sure if this is needed?
        .submit()                 // submit the form
    ;
}

