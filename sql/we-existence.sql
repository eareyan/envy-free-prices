CREATE TABLE we_existence_singleminded( 
	n integer NOT NULL,
	m integer NOT NULL,
	k integer NOT NULL,
	edges text NOT NULL,
	rewards text NOT NULL,
	time double precision NOT NULL,
	we integer NOT NULL
	);

SELECT n,m,k,substring(edges for 30),substring(rewards for 30), time, we FROM we_existence_singleminded;
SELECT n,m,k,substring(edges for 30),substring(rewards for 30), time, we FROM we_existence_singleminded WHERE we = 0;

SELECT COUNT(*) FROM we_existence_singleminded WHERE we = 0;


SELECT COUNT(*) FROM we_existence_singleminded WHERE k > 1 AND k!=n AND we = 0;
SELECT COUNT(*) FROM we_existence_singleminded WHERE k > 1 AND k!=n AND we = 1;



SELECT COUNT(*) FROM we_existence_singleminded_integer_model WHERE k > 1 AND k!=n AND we = 0 AND n > m;
SELECT COUNT(*) FROM we_existence_singleminded_integer_model WHERE k > 1 AND k!=n AND we = 1 AND n > m;



SELECT COUNT(*) FROM we_existence_singleminded WHERE m = 16 AND n = 7 AND k=4 AND we = 0;
