
function combineAndSendForms() {
        var $newForm = $("<form></form>")    // our new form.
            .attr({method: "POST", action: "https://donationserver.herokuapp.com/transaction/"}) // customise as required
        ;
        $(":input:not(:submit, :button)").each(function () {  // grab all the useful inputs
            $newForm.append($("<input type=\"hidden\" />")   // create a new hidden field
                .attr('name', this.name)   // with the same name (watch out for duplicates!)
                .val($(this).val())        // and the same value
            );
        });
        var id = document.getElementsByName("hidden_id").value;
        $newForm.append($("<input type=\"hidden\" />")   // create a new hidden field
                .attr('name', "id")   // with the same name (watch out for duplicates!)
                .val(id)        // and the same value
            );
        $newForm
            .appendTo(document.body)  // not sure if this is needed?
            .submit()                 // submit the form
        ;
    }