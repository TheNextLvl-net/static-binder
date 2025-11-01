import org.jspecify.annotations.NullMarked;

@NullMarked
module net.thenextlvl.binder {
    exports net.thenextlvl.binder;
    
    requires static org.jetbrains.annotations;
    requires static org.jspecify;
}