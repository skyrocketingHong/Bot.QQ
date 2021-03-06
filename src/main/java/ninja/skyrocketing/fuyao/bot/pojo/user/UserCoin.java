package ninja.skyrocketing.fuyao.bot.pojo.user;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.*;

import java.util.Date;

/**
 * @author skyrocketing Hong
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class UserCoin {
    @TableId
    private Long userId;

    private Long coin;

    private Date getDate;
}