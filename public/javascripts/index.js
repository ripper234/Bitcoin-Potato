$(function(){
    $("#to-play").click(function(){
        $("#play-instructions").toggle(300);
    });

    $("#return-address-form").ajaxForm({
    beforeSubmit: function(){
        // Validate bitcoin address
    },
    success: function(){
        alert("Success");
    }})
});