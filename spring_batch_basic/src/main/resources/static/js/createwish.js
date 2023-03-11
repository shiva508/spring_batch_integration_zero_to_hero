console.log("Hello world!");

$(document).ready(function () {
	
	
  $("#createwish").submit(function (event) {
    event.preventDefault();
    Swal.showLoading();
    var nameOfWisher = $("#nameOfWisher").val();
    var nameOfWishe = $("#nameOfWishe").val();
    var heroMessage = $("#heroMessage").val();
    var subHeroMessage = $("#subHeroMessage").val();
    var heroImageimagebase64 = $("#heroImage-image-base64").val();
    var subHeroImageimagebase64 = $("#subHeroImage-image-base64").val();
    var emailOfWisher = $("#emailOfWisher").val();
    var phoneNumber = $("#phoneNumber").val();
    var inputObject = {};
    inputObject.nameOfWishe = nameOfWishe;
    inputObject.nameOfWisher = nameOfWisher;
    inputObject.heroMessage = heroMessage;
    inputObject.subHeroMessage = subHeroMessage;
    inputObject.heroImage = heroImageimagebase64;
    inputObject.subHeroImage = subHeroImageimagebase64;
    inputObject.emailOfWisher = emailOfWisher;
    inputObject.phoneNumber = phoneNumber;
    $.ajax({
      type: "POST",
      contentType: "application/json",
      url: "/wish/createwishrest",
      data: JSON.stringify(inputObject),
      dataType: "json",
      cache: false,
      success: function (data) {
        $("#nameOfWisher").val("");
        $("#nameOfWishe").val("");
        $("#heroMessage").val("");
        $("#subHeroMessage").val("");
        $("#heroImage-image-base64").val("");
        $("#subHeroImage-image-base64").val("");
        $("#emailOfWisher").val("");
        $("#phoneNumber").val("");
        $("#heroImage").val("");
        $("#subHeroImage").val("");
        Swal.close();
        Swal.fire("Wish is saved successfuly");
      },
      error: function (e) {
        console.log(e);
        Swal.close();
      },
    });
  });

$( "#shiva" ).click(function() {
  alert( "Handler for .click() called." );
});

  $("#wisher-image").change(function () {
    display(this, "wisher-image-base64");
  });
  $("#wish-image").change(function () {
    display(this, "wish-image-base64");
  });

  $("#heroImage").change(function () {
    display(this, "heroImage-image-base64");
  });

  $("#subHeroImage").change(function () {
    display(this, "subHeroImage-image-base64");
  });

  function display(input, id) {
    if (input.files && input.files[0]) {
      var reader = new FileReader();
      reader.onload = function (event) {
        $("#" + id + "").attr("value", event.target.result);
        console.log(event.target.result);
      };
      reader.readAsDataURL(input.files[0]);
    }
  }
});
