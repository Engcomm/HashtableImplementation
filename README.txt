This app implements a hashtable. Collision is handled by chaining implemented using linked lists. When max chain length or loading factor is reached, the hashtable is resized and objects are rehashed. 

Bug to fix: how to allow inserting same times without checking so that insertion takes minimal time, but at the same time allow reduce chain size by resizing? 