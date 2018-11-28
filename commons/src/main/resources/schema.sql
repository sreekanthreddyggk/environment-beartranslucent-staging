CREATE TABLE IF NOT EXISTS webtracking_input (
		id SERIAL PRIMARY KEY,
		carrier VARCHAR(3),
		booking_ref VARCHAR(35),
		bl_number VARCHAR(35),
		containerno varchar(13),
		processing_timestamp TIMESTAMP,
		status VARCHAR(100)
	);
	
CREATE TABLE IF NOT EXISTS webtracking_output (
		id SERIAL PRIMARY KEY,
		carrier VARCHAR(3),
		booking_ref VARCHAR(35),
		bl_number VARCHAR(35),
		containerno VARCHAR(13),
  		status VARCHAR(100),
  		status_date timestamp without time zone,
  		status_location VARCHAR(200),
  		status_vessel VARCHAR(100),
  		status_voyage VARCHAR(50),
  		processed_timestamp TIMESTAMP default CURRENT_TIMESTAMP
	);
	
