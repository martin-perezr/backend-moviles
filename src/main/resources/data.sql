INSERT INTO sqlite_sequence (name, seq)
SELECT 'actividades', 1000
WHERE NOT EXISTS (SELECT 1 FROM sqlite_sequence WHERE name = 'actividades');