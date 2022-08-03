package svinerus.buildtogether.aux;

import com.earth2me.essentials.IEssentials;
import com.earth2me.essentials.Worth;
import net.ess3.api.MaxMoneyException;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import svinerus.buildtogether.BuildTogether;

import javax.annotation.Nullable;
import java.math.BigDecimal;

public class EssentialsApi {

    Worth worth;
    IEssentials essPlugin;

    public EssentialsApi(IEssentials essPlugin) {
        this.essPlugin = essPlugin;
        worth = essPlugin.getWorth();
    }

    @Nullable
    public BigDecimal getWorth(Material mat) {
        return worth.getPrice(null, new ItemStack(mat));
    }


}
