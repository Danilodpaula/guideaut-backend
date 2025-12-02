package com.guideaut.project.mail;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/debug/mail")
@Tag(
    name = "Debug Mail",
    description = "Endpoints de teste para envio de e-mails (apenas ambiente de desenvolvimento)."
)
public class MailTestController {

    private final EmailService emailService;

    public MailTestController(EmailService emailService) {
        this.emailService = emailService;
    }

    @PostMapping("/test")
    @Operation(
        summary = "Enviar e-mail de teste de reset de senha",
        description = """
            Envia um e-mail de teste usando o template de código de redefinição de senha.
            Ideal para validar configuração do SMTP (host, porta, usuário e senha).
            """
    )
    public void sendTest(@RequestParam String to) {
        // só pra testar que o envio funciona
        emailService.sendPasswordResetCodeEmail(to, "123456");
    }
}
