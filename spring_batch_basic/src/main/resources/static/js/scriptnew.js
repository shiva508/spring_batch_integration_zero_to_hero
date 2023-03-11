console.log("Hello world!");

$(document).ready(function () {
  getWishesOnload();

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
        dynamicHtmlContentGenerator(data);
        Swal.close();
      },
      error: function (e) {
        console.log(e);
        Swal.close();
      },
    });
  });
  $("#search-form").submit(function (event) {
    event.preventDefault();
    Swal.showLoading();
    var wisherName = $("#wishername").val();
    var yourwishforfriend = $("#yourwishforfriend").val();
    var wisherimagebase64 = $("#wisher-image-base64").val();
    var wishimagebase64 = $("#wish-image-base64").val();
    inputObject = {};
    inputObject.weWishId = $("#weWishId").val();
    inputObject.wisherFriendName = wisherName;
    inputObject.wisherFriendMessaage = yourwishforfriend;
    inputObject.wisherAvtor = wisherimagebase64;
    inputObject.wisherFriendMemory = wishimagebase64;
    console.log(inputObject);
    $.ajax({
      type: "POST",
      contentType: "application/json",
      url: "/wishfriend/createwishfriendrest",
      data: JSON.stringify(inputObject),
      dataType: "json",
      cache: false,
      success: function (data) {
        $("#wishername").val("");
        $("#yourwishforfriend").val("");
        $("#wisher-image-base64").val("");
        $("#wish-image-base64").val("");
        $("#wisher-image").val("");
        $("#wish-image").val("");

        dynamicHtmlContentGenerator(data);
        Swal.close();
        Swal.fire("Wish is saved successfuly");
      },
      error: function (e) {
        console.log(e);
        Swal.close();
      },
    });
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
  function getWishesOnload() {
    Swal.showLoading();
    $.ajax({
      type: "GET",
      contentType: "application/json",
      url: "/wishfriend/getfriendswishes/" + $("#weWishId").val(),
      dataType: "json",
      cache: false,
      success: function (data) {
        dynamicHtmlContentGenerator(data);
        Swal.close();
      },
      error: function (e) {
        console.log(e);
        Swal.close();
      },
    });
  }

  function dynamicHtmlContentGenerator(data) {
    console.log(data);
    var htmlDynamicContent = "";
    var imageDynamicContent = "";
    data.forEach((element) => {
      htmlDynamicContent += '<figure class="testimonial">';
      htmlDynamicContent +=
        '<img class="testimonial-img" alt="' +
        element.wisherFriendName +
        '" src="' +
        element.wisherAvtor +
        '" />';
      htmlDynamicContent +=
        '<blockquote class="testimonial-text">' +
        element.wisherFriendMessaage +
        "</blockquote>";
      htmlDynamicContent +=
        '<p class="testimonial-name">&mdash; ' +
        element.wisherFriendName +
        "</p>";
      htmlDynamicContent += "</figure>";
      imageDynamicContent += '<figure class="gallery-item">';
      imageDynamicContent +=
        '<img src="' +
        element.wisherFriendMemory +
        '" alt="Photo of beautifully arranged food" />';
      imageDynamicContent += "</figure>";
    });
    $(".testimonials").empty();
    $(".testimonials").append(htmlDynamicContent);
    $(".gallery").empty();
    $(".gallery").append(imageDynamicContent);
  }
});
