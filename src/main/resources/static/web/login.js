$(function(){
function login() {

document.getElementById("login_btn").addEventListener("click",function(){
  $.post("/api/login",
         { username: document.getElementById("username").value,
           password: document.getElementById("password").value }).done(function( jqXHR, textStatus ) {
                                                                            document.getElementById("stato").innerHTML= "Success: " + textStatus ;
                                                                       })
           .error(function( jqXHR, textStatus ) {
               document.getElementById("stato").innerHTML= "Failed: " + textStatus ;
          });
});
}



function logout() {

document.getElementById("logout_btn").addEventListener("click",function(){
  $.post("/api/logout");
});

}
login();
logout();
});




