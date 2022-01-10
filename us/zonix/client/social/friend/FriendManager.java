package us.zonix.client.social.friend;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public final class FriendManager {
   private final Set friends = new HashSet();

   public Set getFriends() {
      return this.friends;
   }

   public Set getOnlineFriends() {
      Set friends = new HashSet();
      Iterator var2 = this.friends.iterator();

      while(var2.hasNext()) {
         Friend friend = (Friend)var2.next();
         if (friend.isOnline()) {
            friends.add(friend);
         }
      }

      return friends;
   }
}
