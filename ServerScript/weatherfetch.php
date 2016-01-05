<?php
    $address=$_GET["Address"];
	$city=$_GET["City"];
	$state=$_GET["State"];
    $totalAddress=$address.",".$city.",".$state;
    $url = "https://maps.googleapis.com/maps/api/geocode/xml?address=".urlencode($totalAddress)."&key=AIzaSyBHeOKb0GWoa4-VU5FRs81pWd2qohQl2EI";
	
	$output=file_get_contents($url);
	if($output == false)
	{
		echo "Error while fetching co-ordinates";	
		return;		
	}
    $xmloutput= simplexml_load_string($output);
	if($xmloutput == false)
	{
		echo "Error while parsing";	
		return;
	}

    $latitude = $xmloutput->result[0]->geometry->location->lat;
	$longitude = $xmloutput->result[0]->geometry->location->lng;	    
	
	if($_GET["Temperature"]=="Fahrenheit")
		$units = "us";
	else
		$units = "si";
	
	$forecastUrl = "https://api.forecast.io/forecast/34e86e7ce59329dfa19dcbe121c75e08/".$latitude.",".$longitude."?units=".$units."&exclude=flags";
	$weather=file_get_contents($forecastUrl);
	
	if($weather == false)
	{
		echo "Error while fetching weather";	
		return;		
	}	
	
	//$weather_decoded=json_decode($weather,true);
	echo $weather;
?>