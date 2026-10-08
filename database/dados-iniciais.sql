-- =====================================================================
-- Dados iniciais para um banco NOVO (rode depois de database/schema.sql)
--
-- Por que existe: so um ADMIN consegue cadastrar usuarios pela API, e um banco
-- recem-criado nao tem nenhum. Este script cria o primeiro ADMIN.
--
--   usuario: admin
--   senha:   admin123      <-- somente para desenvolvimento
--
-- ATENCAO: troque essa senha antes de qualquer uso real, por exemplo fazendo
-- login como admin e chamando PUT /usuarios/{id} com uma senha nova.
-- A senha abaixo esta gravada como hash BCrypt, que e o formato que o login espera.
--
--   psql -U postgres -d restaurante -f database/dados-iniciais.sql
-- =====================================================================

INSERT INTO cadastros.usuarios (nome, usuario, senha, tipo_usuario, ativo)
VALUES ('Administrador', 'admin', '$2a$10$Plk.sGR5GANUwML9nsndOuy/xgxUgOKH7nkDtAtmMXrl4xI1xut3u', 'ADMIN', TRUE);
