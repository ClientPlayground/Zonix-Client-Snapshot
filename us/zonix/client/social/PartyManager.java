package us.zonix.client.social;

import java.util.HashSet;
import java.util.Set;

public final class PartyManager {
   private final Set partyMembers = new HashSet();
   private boolean leader;

   public PartyManager() {
      this.partyMembers.add("Erouax");
      this.partyMembers.add("Manthe");
   }

   public Set getPartyMembers() {
      this.partyMembers.clear();
      this.partyMembers.add("Erouax");
      this.partyMembers.add("Manthe");
      this.partyMembers.add("Hitler");
      return this.partyMembers;
   }

   public boolean isLeader() {
      return this.leader;
   }

   public boolean isInParty() {
      return true;
   }
}
