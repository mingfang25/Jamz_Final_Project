## Jamz Music Networking App

Jamz Team: Ricardo Ballesteros | Mingfang Du | Gahouray Dukuray | Lunhao Liang | Shirui Ye

Jamz is an android social networking app for Musicians to be able to find other Musicians in their area to be able to collaborate with. Jamz is solving the problems of local artist struggling to find like artist; as an artist, you may find their are artist in your area, but they may not have the same music taste, or they do not play the same instruments as you, or you are simply trying to find a specific type of musician, Jamz is here for this very purpose.

Artist are able to sign in with their Google or Facebook accounts and start connecting with other artist. Once the user logs in, they are able to perform these functions:

1. View other artist profiles: They can view the artist's music taste(via Spotify API), their recent videos from their Youtube channels, or music and videos the user uploads onto their profile.

2. Message other users

3. Create a Jam(music event)

4. Join in on a group chat for an ongoing Jam/event in their area

5. Browse events that are either Jam Sessions created by other users or events from the TicketMaster API


EXTRA CREDIT/BONUSES
  - App has three languages, targeting three locales: English, Chinese, and Arabic
  - App uses Fragments and RecyclerView
  - Use of Menu(Action Bar)
  - During the demo presentation, we had a live demo with one of our teammates(Ricardo) playing guitar and everyone singing. Then we filmed the scene live and uploaded it to our database. Finally, we show that we can retrieve data by streaming the uploaded video in Jamz App.

NOTE:
  - Before you sign in, make sure your name on Facebook/Google does not have any of the following characters in it, or else the app will crash: "." , "#" , "$" , "[" or "]"
  This is not a fault of Jamz, but a bug in the Firebase API.

REFERENCES

Youtube:
  - https://developers.google.com/youtube/v3/quickstart/android
  - https://github.com/Lipdroid/YoutubeAPIV3

Spotify:
  - https://developer.spotify.com/documentation/general/guides/authorization-guide/
  - https://github.com/spotify/android-sdk

Ticketmaster:
  - https://developer.ticketmaster.com/products-and-docs/apis/discovery-api/v2/#search-events-v2

Firebase(Chat and Data Storage):
  - https://firebase.google.com/docs/android/setup
  - https://codelabs.developers.google.com/codelabs/firebase-android/#0


Other references listed are commented in the code where they are used.
