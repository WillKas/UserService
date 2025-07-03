package vmtecnologia.com.br.UserService.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * Serviço responsável por enviar notificações por e-mail
 * relativas à criação e atualização de usuários.
 *
 * <p>Utiliza JavaMailSender para o envio real de mensagens,
 * e captura exceções para falhas de entrega.</p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String from;

    /**
     * Envia um e-mail de boas-vindas após criação de usuário.
     *
     * @param to       endereço de e-mail do destinatário
     * @param username nome de usuário cadastrado, usado no corpo da mensagem
     * @throws RuntimeException se ocorrer erro ao tentar enviar o e-mail
     */
    public void sendUserCreationEmail(String to, String username) {
        try {
            SimpleMailMessage msg = new SimpleMailMessage();
            msg.setFrom(from);
            msg.setTo(to);
            msg.setSubject("Bem-vindo(a) à nossa aplicação");
            msg.setText(String.format(
                    "Olá %s,\n\n" +
                            "Seu usuário foi criado com sucesso!\n\n" +
                            "Atenciosamente,\nEquipe de Suporte",
                    username
            ));

            mailSender.send(msg);
            log.info("sendUserCreationEmail() -> E-mail de criação enviado para {}", to);
        } catch (Exception e) {
            log.error("sendUserCreationEmail() -> Erro ao enviar e-mail de criação para {}: {}", to, e);
            throw new RuntimeException("Erro ao enviar e-mail de criação", e);
        }
    }

    /**
     * Envia um e-mail informando que os dados do usuário foram atualizados.
     *
     * @param to       endereço de e-mail do destinatário
     * @param username nome de usuário, usado no corpo da mensagem
     * @throws RuntimeException se ocorrer erro ao tentar enviar o e-mail
     */
    public void sendUserUpdateEmail(String to, String username) {
        try {
            SimpleMailMessage msg = new SimpleMailMessage();
            msg.setFrom(from);
            msg.setTo(to);
            msg.setSubject("Dados de conta atualizados");
            msg.setText(String.format(
                    "Olá %s,\n\n" +
                            "Suas informações de usuário foram atualizadas com sucesso!\n\n" +
                            "Atenciosamente,\nEquipe de Suporte",
                    username
            ));

            mailSender.send(msg);
            log.info("sendUserUpdateEmail() -> E-mail de atualização enviado para {}", to);
        } catch (MailException e) {
            log.error("sendUserUpdateEmail() -> Erro ao enviar e-mail de atualização para {}: {}", to, e);
            throw new RuntimeException("Erro ao enviar e-mail de atualização", e);
        }
    }

}
