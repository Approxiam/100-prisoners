This repository contains code to simulate the _100 Prisoners and a Light Bulb_ problem.

# Problem
There's a prison where the yard has a light that can be turned on or off by the prisoners. There are 100 prisoners in solitary confinement which means that they can't interact with each other nor can have any sensory information coming from the outside.

At the time of imprisonment the light bulb is turned off. Every day the warden picks a random prisoner with equal probability and lets them visit the yard where they can toggle the light if they want to.

Every day the selected prisoner has an option to tell the warden that all of the inmates have visited the yard at least once:
 * If the prisoner is right, then the warden sets all of them free.
 * If the prisoner is wrong, then all them are executed.
Note that this is optional, and they can wait until they are 100% certain about their choice.

What strategy should they choose during the transfer to the prison knowing all the above conditions?

# Solutions
See the Protocol classes to see how the prisoners can pick their strategy to minimize the number of days spent in prison.
