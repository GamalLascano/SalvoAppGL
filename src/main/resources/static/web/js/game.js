$(function() {
  // load and display JSON sent by server for /players
    const urlParams = new URLSearchParams(window.location.search);
    $.get("/api/game_view/" + urlParams.get('gp')).done(function(data){
            var item=document.createElement('tr');
            var item2=document.createElement('td');
            item2.appendChild(document.createTextNode(" "));
            item.appendChild(item2);
            var res = [];
            data.ships.forEach(function (dt){
                res = res.concat(dt.locations.map(s=>s.split("")));
            });
            for(var i=1;i<=10;i++){
                item2= document.createElement('td');
                item2.appendChild(document.createTextNode(i));
                item.appendChild(item2);
            }
            document.getElementById('table1').appendChild(item);
            for(var y=1;y<=10;y++){
                item=document.createElement('tr');
                item2=document.createElement('td');
                item2.appendChild(document.createTextNode((y+9).toString(36).toUpperCase()));
                item.appendChild(item2);
                for(var x=1;x<=10;x++){
                        item2= document.createElement('td');
                        res.forEach(function(ele){
                            if((ele[0].localeCompare((y+9).toString(36).toUpperCase())==0)&&(ele[1].localeCompare(x.toString())==0)){
                                item2.appendChild(document.createTextNode("F"));
                                item2.style.backgroundColor = "#0000FF";
                            }else{
                                item2.appendChild(document.createTextNode(" "));
                            }
                        });
                        item.appendChild(item2);
                }
                document.getElementById('table1').appendChild(item);
            }
            document.getElementById("para").innerHTML = data.gamePlayers.map(function(p) {
                var aux = p.player.email;
                if(0 == urlParams.get('gp').localeCompare(p.id.toString())){
                    aux= aux.concat(" (YOU)");
                }
                return aux;
            }).join(" vs ");
    }).fail(function() {
            $("#para").text("No se ha encontrado el jugador deseado");
          });
});