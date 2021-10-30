package at.emielregis.worldeditanimations;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldedit.world.World;
import lombok.SneakyThrows;
import org.bukkit.Location;
import org.bukkit.Material;

public class AnimationFrame {
    private final Location location;
    private final Clipboard clipboard;

    public AnimationFrame(Location loc, Clipboard clip) {
        location = loc;
        clipboard = clip;
    }

    @SneakyThrows
    public void paste() {
        EditSession session = WorldEdit.getInstance().newEditSession(adapt(location.getWorld()));
        Operation operation = new ClipboardHolder(clipboard)
                .createPaste(session)
                .to(BlockVector3.at(location.getX(), location.getY(), location.getZ()))
                .build();
        Operations.complete(operation);
        session.close();
    }

    @SneakyThrows
    public void delete() {
        EditSession session = WorldEdit.getInstance().newEditSession(adapt(location.getWorld()));
        BlockVector3 difference = clipboard.getOrigin().subtract(clipboard.getMinimumPoint());
        Region region = clipboard.getRegion().clone();
        region.shift(region.getMinimumPoint().multiply(-1));
        region.shift(BlockVector3.at(location.getBlockX(), location.getBlockY(), location.getBlockZ()));
        region.shift(difference.multiply(-1));
        session.setBlocks(region, BukkitAdapter.adapt(Material.AIR.createBlockData()));
        session.close();
    }

    public static World adapt(org.bukkit.World world) {
        return new BukkitWorld(world);
    }
}
