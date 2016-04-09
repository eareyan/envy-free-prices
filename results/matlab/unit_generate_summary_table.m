%filename = '/home/eareyanv/workspace/envy-free-prices/results/results-unit_demand.csv';
%filename = '/home/eareyanv/workspace/envy-free-prices/results/results-unit_uniform_demand.csv';
filename = '/home/eareyanv/workspace/envy-free-prices/results/results-fancy_underdemand.csv';
output_folder = '/home/eareyanv/Dropbox/CSCI2980 - Reading and Research/EC16-latex/tables/';
strcat(output_folder,'h')
M = csvread(filename,1);
%M(:,3)
%M = M(M(:,3)==0.25,:)
M = M(M(:,1)>M(:,2) & M(:,3)==1.0,:)
ckEfficiency = mean(M(:,4));
ckRevenue = mean(M(:,5));
ckTime = mean(M(:,6));
ckWE1 = mean(M(:,7));
ckWE2 = mean(M(:,8));

evpEfficiency = mean(M(:,9));
evpRevenue = mean(M(:,10));
evpTime = mean(M(:,11));
evpWE1 = mean(M(:,12));
evpWE2 = mean(M(:,13));

mweqEfficiency = mean(M(:,14));
mweqRevenue = mean(M(:,15));
mweqTime = mean(M(:,16));
mweqWE1 = mean(M(:,17));
mweqWE2 = mean(M(:,18));

%{
lpEfficiency = mean(M(:,19));
lpRevenue = mean(M(:,20));
lpTime = mean(M(:,21));
lpWE1 = mean(M(:,22));
lpWE2 = mean(M(:,23));
%}

%matrix = [ckEfficiency ckRevenue ckTime ckWE1 ckWE2;evpEfficiency evpRevenue evpTime evpWE1 evpWE2;mweqEfficiency mweqRevenue mweqTime mweqWE1 mweqWE2;lpEfficiency lpRevenue lpTime lpWE1 lpWE2]
matrix = [ckEfficiency ckRevenue ckTime ckWE1 ckWE2;evpEfficiency evpRevenue evpTime evpWE1 evpWE2;mweqEfficiency mweqRevenue mweqTime mweqWE1 mweqWE2]
%rowLabels = {'CK', 'EVApp' , 'MaxWEQ','LP'}; 
rowLabels = {'CK', 'EVApp' , 'MaxWEQ'}; 
columnLabels = {'Efficiency','Revenue','Time','WE1','WE2'}; 
matrix2latex(matrix, strcat(output_folder,'out.tex'), 'rowLabels', rowLabels, 'columnLabels', columnLabels, 'alignment', 'c', 'format', '%-6.2f'); 