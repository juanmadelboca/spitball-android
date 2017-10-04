<?php
$mysqli= new mysqli('localhost','id2571301_developer','spitdev','id2571301_spitball')or die("Problemas en la conexion");

$json = file_get_contents('php://input');
$obj = json_decode($json);
$method = $obj->{'METHOD'};

if( $method== 'CREATE'){

$sql="SELECT * FROM GAMES ORDER BY GAMEID DESC LIMIT 0,1";
//Me fijo si la consulta no da error
if (!$resultado=$mysqli->query($sql)){
echo "la consulta fallo";}
$object= new stdClass();
while($row=$resultado->fetch_assoc()){
	if($row['NUMPLAYERS'] ==2){
                $sql= "INSERT INTO GAMES(NUMPLAYERS) VALUES(1)";
		$object->TURN=0;
		$GAMEID=$row['GAMEID']+1;
		$NUMPLAYERS=1;
	}else{
		$sql= "UPDATE GAMES SET NUMPLAYERS=2 ";
		$object->TURN=1;
		$NUMPLAYERS=2;
		$GAMEID=$row['GAMEID'];
	}
	$object->NUMPLAYERS=$NUMPLAYERS;
	$object->GAMEID=$GAMEID;
}
//Me fijo si la consulta no da error

if (!$resultado=$mysqli->query($sql)){
echo "la consulta fallo";}
$json=json_encode($object);
echo $json;

}else{

$sql="SELECT * FROM GAMES ORDER BY GAMEID DESC LIMIT 0,1";
//Me fijo si la consulta no da error
if (!$resultado=$mysqli->query($sql)){
echo "la consulta fallo";}
$object= new stdClass();
while($row=$resultado->fetch_assoc()){
    $object->NUMPLAYERS=$row['NUMPLAYERS'];
    $object->GAMEID=$row['GAMEID'];
}

$json=json_encode($object);
echo $json;

}
?>