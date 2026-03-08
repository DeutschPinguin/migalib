package go.sinzchr.migalib.misc;


public enum Priority
{
        
        HIGHEST("highest", 5),
        HIGH("high", 4),
        NORMAL("normal", 3),
        LOW("low", 2),
        LOWEST("lowest", 1),
        MONITOR("monitor", 0);
        
        
        public static final Priority[] ALL = {HIGHEST, HIGH, NORMAL, LOW, LOWEST, MONITOR};
        
        public final String name;
        public final int index;
        
        
        Priority (String name, int index)
        {
                this.name = name;
                this.index = index;
        }
        
}
