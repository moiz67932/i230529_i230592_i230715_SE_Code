import subprocess
import sys
import os

password = 'password'
sql_path = os.path.join(os.path.dirname(__file__), '..', 'update_admin_password.sql')

# Ensure bcrypt is installed
try:
    import bcrypt
except Exception:
    subprocess.check_call([sys.executable, '-m', 'pip', 'install', 'bcrypt'])
    import bcrypt

# Generate hash
hashed = bcrypt.hashpw(password.encode('utf-8'), bcrypt.gensalt(12)).decode('utf-8')
print('Generated bcrypt hash:')
print(hashed)

# Write SQL file
sql = f"UPDATE users SET password_hash = '{hashed}' WHERE email = 'admin@pipelinex.local';\n"
with open(sql_path, 'w', encoding='utf-8') as f:
    f.write(sql)

print(f'Wrote SQL to {sql_path}')
