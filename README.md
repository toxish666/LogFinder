# LogFinder

Local host findings are tested.

FTP remote findings are half-tested.

Search in one global request happens concurrently.
At the same time SwingWorker feature facilitates concurrency between many global request.

Search for a given string to find happens with the help of Stream, so no files are stored in memory
(1GB files may be processed (theoretically)).

Great enhancements can be made there.

P.S.: time it takes me to accomplish by this exact commit ~ 2 days