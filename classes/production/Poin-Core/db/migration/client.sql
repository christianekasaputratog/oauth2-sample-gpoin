CREATE TABLE CLIENT
(
  id bigint NOT NULL,
  approved boolean,
  client_id character varying(255) NOT NULL,
  client_secret character varying(255) NOT NULL,
  email character varying(255),
  registration_time timestamp without time zone DEFAULT now(),
  web_server_redirect_uri character varying(255),
  CONSTRAINT client_pkey PRIMARY KEY (id),
  CONSTRAINT uk_bfjdoy2dpussylq7g1s3s1tn8 UNIQUE (client_id)
)