package go.sinzchr.migalib.misc;


public enum Status
{
        
        STARTUP("startup"),
        SHUTDOWN("shutdown"),
        ALIVE("alive"),
        DEAD("dead");
        
        public final String name;
        
        
        Status (String name)
        {
                this.name = name;
        }
        
}
