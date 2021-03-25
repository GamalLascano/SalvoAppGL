$(function() {
  // load and display JSON sent by server for /players
  function loadData() {
    $.get("/api/games")
    .done(function(data) {
       data.forEach(function (nod){
              var listViewItem=document.createElement('li');
              listViewItem.appendChild(document.createTextNode(nod.created.replace("T"," ")));
              document.getElementById('gameData').appendChild(listViewItem);

              if(nod.gamePlayers.length >= 1){
                document.getElementById('gameData').appendChild(document.createTextNode(nod.gamePlayers[0].player.email));
              }
              for(var i=1;i<nod.gamePlayers.length;i++){
                document.getElementById('gameData').appendChild(document.createTextNode(", " + nod.gamePlayers[i].player.email));
              }
       });
    })
    .fail(function( jqXHR, textStatus ) {
      showOutput( "Failed: " + textStatus );
    });
  }
  loadData();
});
