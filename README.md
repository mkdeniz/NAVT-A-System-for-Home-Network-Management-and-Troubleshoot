{\rtf1\ansi\ansicpg1252\cocoartf1265
{\fonttbl\f0\froman\fcharset0 Times-Roman;}
{\colortbl;\red255\green255\blue255;\red15\green112\blue1;}
\paperw11900\paperh16840\margl1440\margr1440\vieww10800\viewh8400\viewkind0
\deftab720
\pard\pardeftab720\sa96

\f0\b\fs48 \cf0 A System for Home Network Analysis, Visualisation and Troubleshooting
\b0\fs32 \cf2 \
\pard\pardeftab720\li320\ri700\sa160

\b \cf0 Description:
\b0  \uc0\u8232 Home networks are becoming increasingly complex to manage and troubleshoot due to the presence of wireless and the plethora of devices that connect to them. Especially for the non-expert user, identifying the state of the network at any given time can be cumbersome and sometimes impossible. Even though home network gateways and Operating Systems come with increasing diagnostic and configuration support, still figuring out the state of the network and the cause of errors remains non-trivial. \u8232 The goal of this project is to increase visibility into the state and activity of the home network at any given time in a simple and effective way to the end-user. Using simple heuristics, the system should be able to diagnose common sources of error (e.g., DNS server unreachable, wireless authentication issues, etc.) and to store host activity and bandwidth consumption in a round-robin database. Using temporal statistics, it should be able to profile normal network behaviour and infer causes of anomalies when these occur. \u8232 The system should operate out of a general-purpose PC attached to the home gateway, and use callbacks to report usage statistics to trusted hosts. It should graphically visualise per-host network activity, and would ideally be extensible to accommodate new troubleshooting and diagnostic operations. }