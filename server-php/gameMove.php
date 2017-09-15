<?php
$mysqli = new mysqli('localhost','id2571301_developer','spitdev','id2571301_spitball')or die("Problemas en la conexion");
$json = file_get_contents('php://input');
$obj = json_decode($json);
$method = $obj->{'METHODTYPE'};
$game_id = $obj->{'GAMEID'};
$x_init = $obj->{'XINIT'};
$y_init = $obj->{'YINIT'};
$x_last = $obj->{'XLAST'};
$y_last = $obj->{'YLAST'};
$split = $obj->{'SPLIT'};
$turn = $obj->{'TURN'};

if($method == 'MOVE'){
    $sql="INSERT INTO LOGIC(GAMEID,XINIT,YINIT,XLAST,YLAST,SPLIT,TURN) VALUES('$game_id','$x_init','$y_init','$x_last','$y_last','$split','$turn')";
    //Me fijo si la consulta no da error
    if (!$resultado = $mysqli->query($sql)){
    echo "la consulta fallo";}
    echo "insercion";
}else{
    $sql="SELECT * FROM LOGIC WHERE GAMEID='$game_id'ORDER BY ID DESC LIMIT 0,1";
    //Me fijo si la consulta no da error
    if (!$resultado = $mysqli->query($sql)){
    echo "la consulta fallo";}

    $object = new stdClass();
    while($row=$resultado->fetch_assoc()){
        $object->GAMEID = $row['GAMEID'];
        $object->XINIT =  $row['XINIT'];
        $object->YINIT =  $row['YINIT'];
        $object->XLAST =  $row['XLAST'];
        $object->YLAST =  $row['YLAST'];
        $object->SPLIT =  $row['SPLIT'];
        $object->TURN =  $row['TURN'];
    }
    
    $json=json_encode($object);
    echo $json;
}
?>