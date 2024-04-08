package dto;

import lombok.Data;
import util.TipoEnum;
import java.io.Serializable;

@Data
public class UsuarioDTO implements Serializable {

    private TipoEnum tipo;

    private String login;

}
