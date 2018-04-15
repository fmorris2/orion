# Project Orion

# Overview
Project Orion is the successor of a previous gold-farming endeavor, Project Tesla. Orion was meant to improve on various aspects of Tesla, 
and provide an architecture capable of a lucrative gold-farm. "Gold-farming" in Old School Runescape (OSRS) entails harvesting in-game materials,
trading them for gold, and selling the gold to players for real money. Orion utilized the [OSBot](https://osbot.org/forum/) client.

# Features
- Scalability
  - Orion was built to handle as many "bots" as possible, in order to support a massive gold-farm
  - Whereas Tesla supported a 1-bot-per-server architecture, Orion supports a many-bot-per-server approach
    - The overhead cost for a many-bot-per-server approach is much lower
  - On each dedicated server, the [Orion Cluster Controller](https://github.com/fmorris2/orion-cluster-controller) (OCC) would be ran in the background. The OCC helped conserve resources on the database server by batching SQL queries and commands together.
- Flexibility
  - Orion supports various different in-game activities to choose from when running the gold-farm
    - Each activity is represented in this repository by a designated submodule
    - It is very easy to add new activities to the Orion architecture
- Portability
  - Orion supports both Unix-based & Windows-based platforms
