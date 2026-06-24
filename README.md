# ProductList

## App Architecture

----

### For User Events
#### Ui &rarr; ProductViewModel &rarr; EventRepository &rarr; EventDao &rarr; Room
Events stored in room database with the flag ` isSynced ` ` false `.

#### EventUploadWorker &rarr; EventRepository &rarr; EventDao &rarr; Room
Events are marked as ` isSynced ` ` true ` after uploading to the server.


