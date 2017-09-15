#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Created on Sat Sep  9 10:39:53 2017

@author: enriqueareyan
"""

import pandas as pd
import matplotlib.pyplot as plt
import numpy as np

for valuation in [('sizeinter_uniform','SI'),
                  ('singleminded_uniform','Single-minded'),
                  ('singleton_uniform', 'Singleton')]:
    data = pd.read_csv(valuation[0] + '.csv')
    data = data[['m','gwwelfare','owwelfare']]
    data['ratio'] = data['owwelfare'] / data['m']
    #data['ratio'] = data['owwelfare'] / np.sqrt(data['m'])

    #data = data[['m','gwrevenue','owrevenue']]
    #data['ratio'] = data['owrevenue'] / data['m']
    
    summary = data.groupby(by='m').mean()

    plt.plot(summary['gwwelfare'], label = 'LP, ' + valuation[1])
    #plt.plot(summary['gwrevenue'], label = 'LP, ' + valuation[1])
    plt.plot(summary['ratio'], label = 'OPT/m, ' + valuation[1])

plt.title('Approximation v Theoretical Bound (Welfare)')
#plt.title('Approximation v Theoretical Bound (Revenue)')
plt.ylabel('Welfare')
plt.xlabel('Number of Bidders')
plt.legend()