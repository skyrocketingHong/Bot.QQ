package ninja.skyrocketing.fuyao.bot.pojo.bot;

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
public class BotBanedUser {
    @TableId
    private Long userId;

    private Long addUser;

    private Date addDate;
}