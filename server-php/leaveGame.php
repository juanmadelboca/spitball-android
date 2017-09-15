<?php

$mysqli= new mysqli('localhost','id2571301_developer','spitdev','id2571301_spitball')or die("Problemas en la conexion");
$json = file_get_contents('php://input');
$obj = json_decode($json);
$game_id = $obj->{'METHOD'};
$sql="DELETE FROM GAMES WHERE GAMEID='$game_id'";
//Me fijo si la consulta no da error
if (!$resultado=$mysqli->query($sql)){
echo "la consulta fallo";}
?>