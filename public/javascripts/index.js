var formSubmittedTime;
$(function(){
    $("#to-play").click(function(){
        $("#play-instructions").toggle(300);
    });

    $("#return-address-form").ajaxForm({
    beforeSubmit: function(){
        var returnAddress = $("#returnAddress").val();
        if (returnAddress.length < 5) {
            alert("Invalid Bitcoin Address");
            return false;
        }

        $("#return-address-form input[type='submit']").attr("disabled", "disabled");
        $("#return-address-form .loading").show();
        formSubmittedTime = new Date().getTime();
    },
    success: function(data){
        var sleepTime = 1000 - (new Date().getTime() - formSubmittedTime);
        sleepTime = Math.max(0, sleepTime);

        setTimeout(function(){
            $("#return-address-form .loading").hide();
            $("#return-address-form .completed").show();
            $("#payToPlayAddress").text(data);
        }, sleepTime);
    }})
});