using Microsoft.Extensions.Logging;
using LocationService.Core.Port;

namespace LocationService.Infrastructure.Repository.Adapter;

public class DbTransactionAdapter : IDbTransactionPort
{
    private readonly LocationDbContext _dbContext;
    private readonly ILogger<DbTransactionAdapter> _logger;

    public DbTransactionAdapter(LocationDbContext dbContext, ILogger<DbTransactionAdapter> logger)
    {
        _dbContext = dbContext;
        _logger = logger;
    }

    public async Task<T> ExecuteInTransactionAsync<T>(Func<Task<T>> operation, CancellationToken cancellationToken = default)
    {
        if (_dbContext.Database.CurrentTransaction != null)
        {
            _logger.LogDebug("Using existing transaction");
            return await operation();
        }

        _logger.LogDebug("Beginning new transaction");
        await using var transaction = await _dbContext.Database.BeginTransactionAsync(cancellationToken);
        try
        {
            var result = await operation();
            await transaction.CommitAsync(cancellationToken);
            _logger.LogInformation("Transaction committed successfully");
            
            return result;
        }
        catch (Exception ex)
        {
            _logger.LogWarning(ex, "Transaction rolled back due to error: {ErrorMessage}", ex.Message);
            await transaction.RollbackAsync(cancellationToken);
            throw;
        }
    }

    public async Task ExecuteInTransactionAsync(Func<Task> operation, CancellationToken cancellationToken = default)
    {
        if (_dbContext.Database.CurrentTransaction != null)
        {
            _logger.LogDebug("Using existing transaction");
            await operation();
            return;
        }

        _logger.LogDebug("Beginning new transaction");
        await using var transaction = await _dbContext.Database.BeginTransactionAsync(cancellationToken);
        try
        {
            await operation();
            await transaction.CommitAsync(cancellationToken);
            _logger.LogInformation("Transaction committed successfully");
        }
        catch (Exception ex)
        {
            _logger.LogWarning(ex, "Transaction rolled back due to error: {ErrorMessage}", ex.Message);
            await transaction.RollbackAsync(cancellationToken);
            throw;
        }
    }
}