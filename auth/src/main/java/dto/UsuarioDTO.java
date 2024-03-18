package dto;

import util.TipoEnum;
import java.io.Serializable;

public class UsuarioDTO implements Serializable {

    private TipoEnum tipo;

    private String login;

    public TipoEnum getTipo() {
        return tipo;
    }

    public String getLogin() {
        return login;
    }
}
