<?php 
$conexion=mysql_connect("localhost","root","")or die("Problemas en la conexion");
mysql_select_db("SpitBall",$conexion) or die ('Problemas en la seleccion de base');

$tabla=mysql_query("SELECT * FROM SCORES",$conexion);
$data_array=array();
while($row=mysql_fetch_array($tabla)){

		$object->PLAYER=$row['PLAYER'];
		$object->SCORE=$row['SCORE'];
		$data_array[]=$object;
	}
	$json =json_encode($data_array);
	echo $json;
		
?>