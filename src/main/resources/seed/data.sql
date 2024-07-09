INSERT INTO vehicle (number, driver_name, make, model, year, created_date, last_modified_date)
SELECT * FROM (
    SELECT 'ABC-123', 'Test Driver 1', 'Honda', 'Civic', '2020', now() as created_date, now() as last_modified_date
    UNION ALL
    SELECT 'BCD-145', 'Test Driver 2', 'Honda', 'Civic', '2020', now() as created_date, now() as last_modified_date
    UNION ALL
    SELECT 'XYZ-345', 'Test Driver 3', 'Honda', 'Civic', '2020', now() as created_date, now() as last_modified_date
    UNION ALL
    SELECT 'FGE-321', 'Test Driver 4', 'Audi', 'Cabriolet', '2022', now() as created_date, now() as last_modified_date
    UNION ALL
    SELECT 'BCD-455', 'Test Driver 5', 'Audi', 'Cabriolet', '2022', now() as created_date, now() as last_modified_date
) AS temp
WHERE NOT EXISTS (
    SELECT 1 FROM vehicle
);


INSERT
INTO
  cleaning_professional
  (name, email, image_url, rating, vehicle_id, created_date, last_modified_date)
SELECT * FROM (
    SELECT 'Miguel Munoz', 'davidsavage@example.com', 'https:/example.com/933', 3.1, 1, now() as created_date, now() as last_modified_date
    UNION ALL
    SELECT 'Ashley Johnson', 'williamstamara@example', 'https://example.com/734', 4.9, 1, now() as created_date, now() as last_modified_date
    UNION ALL
    SELECT 'David Griffith', 'pmiller@example.com', 'https://www.example.com/85', 3.7, 1, now() as created_date, now() as last_modified_date
    UNION ALL
    SELECT 'Brittany Romero', 'tyler41@example.com', 'https://www.example.com/121', 4.2, 1, now() as created_date, now() as last_modified_date
    UNION ALL
    SELECT 'Brian Fowler', 'fosterjoseph@example.com', 'https://example.com/135', 3.0, 1, now() as created_date, now() as last_modified_date
    UNION ALL
    SELECT 'Amanda Martinez', 'ritabarr@example.com', 'https://example.com/803', 4.3, 2, now() as created_date, now() as last_modified_date
    UNION ALL
    SELECT 'Matthew Riggs', 'ikeller@example.com', 'https://example.com', 3.8, 2, now() as created_date, now() as last_modified_date
    UNION ALL
    SELECT 'Tonya Hill', 'kathleenharrison@example.com', 'https://example.com/839', 3.6, 2, now() as created_date, now() as last_modified_date
    UNION ALL
    SELECT 'James Hernandez', 'gbailey@example.com', 'https://www.example.com/65', 4.7, 2, now() as created_date, now() as last_modified_date
    UNION ALL
    SELECT 'Erin Alvarez', 'hlopez@example.com', 'https://example.com/882', 4.4, 2, now() as created_date, now() as last_modified_date
    UNION ALL
    SELECT 'Allison Cole', 'jordansmith@example.com', 'https://example.com/405', 4.0, 3, now() as created_date, now() as last_modified_date
    UNION ALL
    SELECT 'Dylan Olson', 'mistyallen@example.com', 'https://example.com/986', 2.9, 3, now() as created_date, now() as last_modified_date
    UNION ALL
    SELECT 'Andrea Gibson', 'edwinmurphy@example.com', 'https://example.com/657', 4.5, 3, now() as created_date, now() as last_modified_date
    UNION ALL
    SELECT 'Christina Pope', 'lucashannah@example.com', 'https://example.com/407', 3.7, 3, now() as created_date, now() as last_modified_date
    UNION ALL
    SELECT 'Jeffrey Randall', 'ymckinney@example.com', 'https://example.com/739', 4.8, 3, now() as created_date, now() as last_modified_date
    UNION ALL
    SELECT 'Michele Velazquez', 'walterschelsea@example.com', 'https://example.com', 4.4, 4, now() as created_date, now() as last_modified_date
    UNION ALL
    SELECT 'Kevin Willis', 'jasminelopez@example.org', 'https://example.com/271', 3.8, 4, now() as created_date, now() as last_modified_date
    UNION ALL
    SELECT 'Crystal Allen', 'briannunez@example.com', 'https://example.com/995', 4.1, 4, now() as created_date, now() as last_modified_date
    UNION ALL
    SELECT 'Amanda Davis', 'carolynbell@example.com', 'https://www.example.com/864', 3.3, 4, now() as created_date, now() as last_modified_date
    UNION ALL
    SELECT 'Teresa Davis', 'hendersonamanda@example.com', 'https://example.com/523', 3.6, 4, now() as created_date, now() as last_modified_date
    UNION ALL
    SELECT 'Richard Smith', 'garnerdarlene@example.com', 'https://www.example.com/433', 3.1, 5, now() as created_date, now() as last_modified_date
    UNION ALL
    SELECT 'Miranda Holmes', 'stantoncaitlyn@example.com', 'https://www.example.com/403', 4.4, 5, now() as created_date, now() as last_modified_date
    UNION ALL
    SELECT 'Russell Reyes', 'ashley76@example.com', 'https://example.com/642', 4.3, 5, now() as created_date, now() as last_modified_date
    UNION ALL
    SELECT 'Erica Gomez', 'smithalice@example.com', 'https://example.com/444', 3.4, 5, now() as created_date, now() as last_modified_date
    UNION ALL
    SELECT 'Linda Turner', 'meghan47@example.com', 'https://example.com', 4.9, 5, now() as created_date, now() as last_modified_date
) AS temp
WHERE NOT EXISTS (
    SELECT 1 FROM cleaning_professional
);


INSERT INTO schedule_configuration
              (start_time, end_time, break_duration_minutes, holiday, created_date, last_modified_date)
SELECT '8:00', '22:00', 30, 'FRIDAY', now(), now()
WHERE NOT EXISTS (
    SELECT 1 FROM schedule_configuration
);



INSERT
INTO
  customer
  (name, email, phone, address, image_url, created_date, last_modified_date)
SELECT 'Haris', 'haris@example.com', '+921234567891', 'Abc street', "https://example.com/555", now(), now()
WHERE NOT EXISTS (
    SELECT 1 FROM customer
);