package com.example.demo;

import java.sql.Timestamp;
import java.util.Date;
import java.util.Optional;

public  class Tools 
{
	public static Object getBean(Optional<?> optional)
	{
		return optional.get();
	}
	public static Timestamp getTimeStamp()
	{
		Date timeDate = new Date(System.currentTimeMillis());
        Timestamp  timestamp = (new Timestamp(timeDate.getTime()));
        return timestamp;
	}
}
