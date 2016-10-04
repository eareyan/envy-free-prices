function [N,R,I,E] = createRandomMarket(n,m)
% createRandomMarket given number of bidders and items, creates random
%                    market.
%         [N,R,I,E] = createRandomMarket(n,m) produces the supply, reward, 
%         demand vector and connection matrix of a random market with n
%         items and m bidders.
    R = 10 .* rand(m,1) + 1;
    I = randi([1,10],m,1);
    N = randi([1,10],n,1);
    E = randi([0 1], n,m);
end