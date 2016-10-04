function X = singleStepAllocation(N,R,I,E)
% singleStepAllocation given a size interchangable market, produces a
%                      greedy allocation.
%       X = singleStepAllocation(N,R,I,E) produces an allocation for the
%       market defined by supply vector N, reward vector R, demand vector
%       I, and connection matrix E.
%
%       Example input:      R = 10 .* rand(4,1) + 1;
%                           I = randi([1,10],4,1);
%                           N = randi([1,10],3,1);
%                           E = randi([0 1], 3,4);
    % Initialize an empty allocation.
    X = zeros(size(N,1),size(R,1)); 
    % Sort bidders by reward over square root of demand in descending order
    Z = sortrows(horzcat( R ./ sqrt(I) , (1:size(R,1))') , -1);
    % For each bidder in the ordering.
    for j=Z(:,2)'
        % Compute if there are enough items to satisfy bidder j.
        if E(:,j)' * (N - X(:,j)) >= I(j)
            % This anom. function computes the allocation for pair (i,j).
            alloc = @(i,j,X,N) min(I(j) - sum(X(:,j),1), E(i,j) * (N(i) - sum(X(i,:),2)));
            % Sort items by ascending order of remaining supply.
            Y = sortrows(horzcat( N - sum(X,2) , (1:size(N,1))') , 1);
            for i= Y(:,2)'
                X(i,j) = alloc(i,j, X, N);
            end
        end
    end
end