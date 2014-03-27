A System for Home Network Analysis, Visualisation and Troubleshooting
---------------------------------------------------------------------
Home networks are becoming increasingly complex to manage and troubleshoot due to the presence of wireless and 
the plethora of devices that connect to them. Especially for the non-expert user, identifying the state of the
network at any given time can be cumbersome and sometimes impossible. Even though home network gateways and 
Operating Systems come with increasing diagnostic and configuration support, still figuring out the state of the
network and the cause of errors remains non-trivial. The goal of this project is to increase visibility
into the state and activity of the home network at any given time in a simple and effective way to the end-user.
Using simple heuristics, the system should be able to diagnose common sources of error (e.g., DNS server 
unreachable, wireless authentication issues, etc.) and to store host activity and bandwidth consumption in a 
round-robin database. Using temporal statistics, it should be able to profile normal network behaviour and infer
causes of anomalies when these occur. The system should operate out of a general-purpose PC attached to
the home gateway, and use callbacks to report usage statistics to trusted hosts. It should graphically visualise
per-host network activity, and would ideally be extensible to accommodate new troubleshooting and diagnostic 
operations.

http://pastebin.com/rDa8nGwP
