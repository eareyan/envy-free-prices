CREATE TYPE distribution_type AS ENUM ('Uniform','Elitist'); ' Couldnot  make this to work with java '
CREATE TABLE complete_search_singleminded( 
	i integer NOT NULL,
	n integer NOT NULL,
	m integer NOT NULL,
	k integer NOT NULL,
	distribution character(7) NOT NULL,
	time double precision NOT NULL,
	num_explored_states double precision NOT NULL,
	num_infeasible double precision NOT NULL,
	num_revbound double precision NOT NULL
	);
