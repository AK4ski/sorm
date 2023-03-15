package org.sorm.db;

import org.h2.tools.Server;

public class H2DBLauncher {
   public static void main(String[] args) throws Exception {
      Server.main();
      System.out.println("DB Launched");
   }
}
