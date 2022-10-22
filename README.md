# Introduction
The reasoning engine for [Sun SPOT](https://www.sunspotdev.org/) nodes is an implementation of the Observer-Reasoner-Actuator (ORA) Mediator designed as part of the [DEMANES](https://artemis-ia.eu/project/37-demanes.html) European research project. This implementation includes a new self-adaptive transmission power developed by GRyS (UPM) and CAR (CSIC).

The self-adaptive transmission power controller uses a double self-adaptive control loop, as shown in the next figure. This control loop can be divided in three main columns, as shown, identified each one as the observer, reasoning and acting parts respectively.

![image](https://user-images.githubusercontent.com/13553876/197353957-370d68c2-f319-43c6-8857-6cab1692a591.png)

The goal for this transmission power controller is to enable each node deployed in a Wireless Sensor Network (WSN) to self-adapt their own transmission power autonomously, based just on the number of detected neighbors and their own battery levels. The rationale is that by reducing the transmission power while guaranteeing a minimum number of neighbors, the total energy consumption of the network can be reduced. Besides, the network error rate can also be improved because of the reduction of the chance of collisions in the coverage area for each node.

This controller was validated in an open scenario, obtaining promising results. Indeed, on one of the configurations tried during the experiments we obtained an 11% improvement in the total energy consumption with respect to the use of a fixed transmission power. And with the same configuration, the packet delivery rate achieved a 99%.

The next figure shows the results for the total energy consumption for each of the experiments. Experiments _e01_ and _e02_  were control experiments using a fixed transmission power. As can be observed, the lowest total energy consumption was for experiment _e02_ (lowest fixed transmission power), closely followed by experiment _e05_ (using the self-adaptive transmission power controller). However, it is also important to take into account the network connectivity.

![image](https://user-images.githubusercontent.com/13553876/197354345-8ce67a72-4344-429b-b415-d0bbe9d444fc.png)

The next figure shows the results for the packet delivery rate for each of the experiments. As can be observed, the best fixed transmission power concerning the total energy consumption (_e02_) had a very poor packet delivery rate, reaching around a 70% at the end of the experiment. However, experiment _e05_, the best for total energy consumption using the self-adaptive transmission power, was also the best regarding the packet delivery rate, with a final value over being 99%, and surpassing the packet delivery rate of experiment _e01_ (fixed maximum transmission power).

![image](https://user-images.githubusercontent.com/13553876/197354355-8024109c-4b9a-4502-825e-40c8b2212622.png)

> Using our self-adaptive transmission power controller, we achieved an 11% improvement in the total energy consumption, and a 99% packet delivery rate.

# Technology Readiness Level (TRL)
This software has reached a TRL 5: Technology validated in relevant environment ([EU definition](https://ec.europa.eu/research/participants/data/ref/h2020/wp/2014_2015/annexes/h2020-wp1415-annex-g-trl_en.pdf)).

# Usage
## Requirements:
- Sun SPOT nodes updated to version 7.0 (Teal).  Previous versions have an issue in the reading of the battery level that is required for the power controller to properly perform the self-adaptation.
- Sun SPOT SDK version 7.0 (Teal).

## Use
To deploy the controller in a Sun SPOT node, use the usual procedure to deploy a MIDlet into the Sun SPOT node:
1. Download the transmission power controller source from the repository:
```
git clone https://github.com/grys-upm/Reasoning-Engine-for-SunSPOT.git
```
2. Move inside the folder of the cloned repository.
3. Connect the Sun SPOT to your desktop machine using a mini-USB cable.
4. Check communication with your Sun SPOT using the ant info command, which displays information about the device.
5. To deploy the transmission power controller application, use the `ant deploy` command.
6. To run the transmission power controller application, use the `ant run` command.

# Technical description and Validation
For a detailed technical description and validation of this transmission power controller, please refer to our related published papers:

- [Self-Adaptive Strategy Based on Fuzzy Control Systems for Improving Performance in Wireless Sensors Networks](https://doi.org/10.3390/s150924125)
- [Communication Range Dynamics and Performance Analysis for a Self-Adaptive Transmission Power Controller](https://doi.org/10.3390/s16050684)

# License
Parts copyrighted by Universidad Politécnica de Madrid (UPM) are distributed
under a dual license scheme:

- For academic uses: Licensed under GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
- For any other use: Licensed under the Apache License, Version 2.0.

Parts copyrighted by DEMANES are distributed under the Apache License, Version 2.0.

For further details on the license, please refer to the [License](License) file.

# Intellectual Property
This software has been registered as an intellectual property by their authors under the registration M-007848/2015 at the Intellectual Property Registry of Madrid (Spain).
