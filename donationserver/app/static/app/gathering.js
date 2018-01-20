$(document).ready(function () {
    $("#slider").slider({
        range: "min",
        animate: true,
        value: 1,
        min: 0,
        max: 10,
        step: 0.005,
        slide: function (event, ui) {
            update(1, ui.value); //changed
        }
    });

    //Added, set initial value.
    $("#amount").val(0);
    $("#amount-label").text(0);


    update();
});

//changed. now with parameter
function update(slider, val) {
    //changed. Now, directly take value from ui.value. if not set (initial, will use current value.)
    var $amount = slider == 1 ? val : $("#amount").val();

    /* commented
     $amount = $( "#slider" ).slider( "value" );
     $duration = $( "#slider2" ).slider( "value" );
     */

    $("#amount").val($amount);
    $("#amount-label").text($amount);

    $('#slider a').html('<label>' + $amount + '</label><div class="ui-slider-label-inner"></div>');
}

$(document).ready(function () {
    $('#datepicker').datepicker({
        uiLibrary: 'bootstrap'
    });
});

function combineAndSendForms() {
        var $newForm = $("<form></form>")    // our new form.
            .attr({method: "POST", action: "https://donationserver.herokuapp.com/creategathering/"}) // customise as required
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